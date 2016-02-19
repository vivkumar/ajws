PWD=`pwd`
OUTDIR=$PWD/results
####################################################
####################################################
if [ $# -lt 1 ]
then
  echo "USAGE: ./execute  <WS_THREADS"
  exit
fi

name="jmetal.experiments.studies.StandardStudy"

java -Dout.dir=$OUTDIR -cp ./jmetal.jar $name $1
