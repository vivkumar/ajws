JIKESRVM=~/vivek_work/work-stealing/rvm/production_base/rvm
OUTDIR=~/vivek_work/work-stealing/jastadd/sourceforge/jmetal/results
TOPDIR=~/vivek_work/work-stealing/jastadd/sourceforge/jmetal
####################################################
####################################################
if [ $# -lt 1 ]
then
  echo "USAGE: ./execute  <WS_THREADS"
  exit
fi

name="jmetal.experiments.studies.StandardStudy"

$JIKESRVM -X:availableProcessors=$1 -Xms921M -Dout.dir=$OUTDIR -X:gc:threads=1 -cp ./jmetal.jar $name $1 $2
