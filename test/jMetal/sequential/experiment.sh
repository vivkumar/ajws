PWD=`pwd`
OUTDIR=$PWD/results
####################################################
####################################################
name="jmetal.experiments.studies.StandardStudy"

java -Dout.dir=$OUTDIR -cp ./jmetal-seq.jar $name
