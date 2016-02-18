JIKESRVM=~/vivek_work/work-stealing/jikesrvm-release/dist/production/rvm
OUTDIR=~/vivek_work/work-stealing/jastadd/sourceforge/jmetal/results
TOPDIR=~/vivek_work/work-stealing/jastadd/sourceforge/jmetal
####################################################
####################################################
name="jmetal.experiments.studies.StandardStudy"

$JIKESRVM -Xms1024M -Dout.dir=$OUTDIR -X:gc:threads=1 -cp ./jmetal-seq.jar $name
