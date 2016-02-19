# Vivek Kumar <vivek.kumar@anu.edu.au>
#
# This script does the following:
# 1) Find all the files, which needs to be translated
#    a) Intitally create the list based on the annotations 
#	(@Atomic, @AliasAtomic, syncsteal, steal)
#    b) Then search all other files and see if they either 
#       extends/implements any of the files from step-a
#    c) Create a new list of files and redo step-b, with 
#	this new list of files. Stop if no longer any files
#	are found. We redo step-b because any class/interface
#	which extends/implements any class/interface with ajws
#	has to be translated.
# 2) Copy all the required packages: "ajws" and "p" (JUtils)
# 3) Translate all the files found in step-1
# 4) Dont re-translate the jutils, use the pre-translated ones for compilation
#

#!/usr/bin/perl -w
use threads;
use strict;

my $home = (getpwuid 501)[7];

my $pwd =  `pwd`;
chomp $pwd;
my $ajws = $pwd . "/../../..";

########### CHANGE THESE VARIBLES BEFORE RUNNING THIS SCRIPT #############
my $path_to_topPackage = "basesrc";
my $TOP_PACKAGE = ".";
my $JASTADD= $ajws . "/src";
my $path_to_classes = "bin";
## --> Add basic dependencies to this classpath
# Absolute path only
my $classpaths = $JASTADD. "/AJWS.jar:" . $pwd . "/../jars/jksvm.jar";
################### DONT CHANGE ANY THING BELOW THIS #####################
##########################################################################

$classpaths = "$classpaths:$pwd/lib/asm-3.0.jar:$pwd/lib/cobertura.jar:$pwd/lib/jakarta-oro-2.0.8.jar:$pwd/lib/junit-4.8.2.jar:$pwd/lib/asm-tree-3.0.jar:$pwd/lib/hamcrest-core-1.2.jar:$pwd/lib/log4j-1.2.9.jar:.";
$path_to_topPackage = $pwd . "/" . $path_to_topPackage;
$path_to_classes = $pwd . "/" .  $path_to_classes;

system("rm -rf $path_to_classes 2>/dev/null");
my $scratch = $path_to_classes . "/scratch";
my $generated = $pwd . "/src";
my $mv_gen = "mkdir -p " . $generated;
system("$mv_gen"); 

chdir $path_to_topPackage or die "Can't chdir $path_to_topPackage: $!";

system("rm *.i 2>/dev/null");

# First copy all the files asis to the generated directory
system("mkdir -p $generated");
my $total_package = $path_to_topPackage . "/" . $TOP_PACKAGE;
system("cp -rf $total_package $generated/");

# Here we will only inlude files with @Atomicsets / @Atomic / @AliasAtomic annotations.
system("grep -r '\@Atomic\\|\@AliasAtomic' $TOP_PACKAGE | sed 's/:/ /' | awk '{print \$1}' | sort | uniq > translate.i");
system("for i in `cat translate.i`; do basename \$i | sed 's/\.java//' >> atomic_classes.i; done");
system("grep -rL '\@Atomic\\|\@AliasAtomic' $TOP_PACKAGE > other_files.i");

print "Calculating Files To Translate.....";
open(TRANSLATE, ">>translate.i");
while(1) {
  open(OTHER_FILES, "other_files.i");
  open(TEMP_FILES, ">>temp_files.i");
  open(TEMP_CLASSES, ">>temp_classes.i");
  
  my $reIterate = 0;
 
  foreach my $line_in_other_files (<OTHER_FILES>) {
    my $found = 0; 
    open(LINE, $line_in_other_files);
    my @contents = grep /implements/ || /extends/, <LINE>;
    close LINE;
    open(CLASSES, "atomic_classes.i");
    foreach my $classes (<CLASSES>) {
      # remove newline
      chomp $classes;
      foreach (@contents) {
        if($_ =~ /$classes/) {
          $found = 1;
	  $reIterate = 1;
          last;
        }
      }  
      if($found == 1) {
	# Add this file in the translate list
        print TRANSLATE $line_in_other_files;
	# Add the class name of this file in the temp class file
	my $new_class = $line_in_other_files;
     	$new_class =~ s{.*/}{};      # removes path
	$new_class =~ s{\.[^.]+$}{}; # removes extension
	print TEMP_CLASSES "$new_class \n";
        last;
      }
    }
    close CLASSES;
    if($found == 0) {
      # as this file is not found, add it to the temp_files list
      print TEMP_FILES $line_in_other_files;
    } 
  }
  close TEMP_CLASSES;
  close TEMP_FILES;
  close OTHER_FILES;
  if($reIterate == 0) {
    # When nothing is found, exit the loop.
    # we are done making the file list for translation.
    last;
  }
  else {
    #rename the temp_files as other_files
    system("mv temp_files.i other_files.i");
    #rename the class files
    system("mv temp_classes.i atomic_classes.i");
  }
} 
close TRANSLATE;

# Now include files with steal / syncsteal annotations
system("grep -r 'finish\\|async' $TOP_PACKAGE | sed 's/:/ /' | awk '{print \$1}' | sort | uniq > steal_files.i");
# create array of files from this file
open(STEAL, "steal_files.i");
my @all_steals =  <STEAL>;
close STEAL;
# Array of files with atomic annotations
open(TRANSLATE, "translate.i");
my @all_translates =  <TRANSLATE>;
close TRANSLATE;
# Create union of these two arrays
my @total_files = keys %{ { map { $_, 1} (@all_steals, @all_translates) } };
# we need to include all the unique files from steal_files.i into translate.i

print "DONE\n";
print "Using two Threads for translating $#total_files files...\n";

# Copy the main class from JastAdd directory
system("cp $JASTADD/JavaPrettyPrinter*.class .");

# Create threads to perform translatation in parallel

# create two array of files to translate (one for each thread)
my @translate_left = splice(@total_files, 0, int(@total_files/ 2));
my @translate_right = @total_files; # the remaining ones

# Thread - 1
my $thread_1 = threads->new(\&doOperation,@translate_left);
# Thread - 2
my $thread_2 = threads->new(\&doOperation,@translate_right);

# Join both the threads
$thread_1->join;
$thread_2->join;

# copy the files related to OrderedLock
system("cp -rf $JASTADD/lib/ajws $generated/");

system("rm *.i 2>/dev/null");
system("rm -rf p 2>/dev/null");
system("rm JavaPrettyPrinter*.class 2>/dev/null");


################## SUBROUTINE ####################
#sub doOperation(@a){
sub doOperation {
  #my (@translate_list) = shift;
  my (@translate_list) = @_;

  # Get the thread id. Allows each thread to be identified.
  my $id = threads->tid();
  my $stdout = "./stdout." . $id . ".i";
  my $stderr = "./stderr." . $id . ".i";
  #Now translate the files
  foreach (@translate_list) {
    my $file_name = $_;
    chomp $file_name;
    print "[T-$id] JikesWSGenerator: Translating input file $file_name\n";
    my $javaCmd = "java -classpath " . $classpaths . " JavaPrettyPrinter " . $file_name . " 1>" . $stdout . " 2>" . $stderr;
    system("$javaCmd");
    # Check if there are any errors
    my $errors = 0;
    open(ERRORS, "stderr.i");
    foreach my $err_line (<ERRORS>) {
      if((lc $err_line) =~ /error/) {
        print "ERROR: Translation failed for File $file_name\n";
        $file_name =~ s{.*/}{}; # removes path
        my $log_file = $scratch . "/" . $file_name . ".err";
        print "LOGFILE=$log_file\n";
        my $mv_err = "mkdir -p " . $scratch . ";" . "mv " . $stderr . " " . $log_file;
        system("$mv_err");
        $errors = 1;
        last;
      }
    }
    last if($errors == 1);

    # move the translated file
    my $genFile = $generated . "/" . $file_name;
    system("mkdir -p `dirname $genFile`");
    system("mv $stdout $genFile");
  }

  # Exit the thread
  threads->exit();
}
