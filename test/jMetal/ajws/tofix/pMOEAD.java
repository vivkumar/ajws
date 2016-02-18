package jmetal.metaheuristics.moead;
import jmetal.core.*;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ajws.OrderedLock;
public class pMOEAD extends Algorithm implements ajws.Atomic {
public final OrderedLock getLockForP() {
return _$lock_P;
}
public final OrderedLock getLock() {
return this.getLockForP();
}
protected final OrderedLock _$lock_P;
  private pMOEAD parentThread_;
  private int populationSize_;
  private SolutionSet population_;
  double[] z_;
  double[][] lambda_;
  int T_;
  int[][] neighborhood_;
  double delta_;
  int nr_;
  Solution[] indArray_;
  String functionType_;
  int evaluations_;
  int maxEvaluations_;
  Operator crossover_;
  Operator mutation_;
  public HashMap<String, Object> map_;
  String dataDirectory_;
  long initTime_;
  public pMOEAD(Problem problem) {
  this(problem, new OrderedLock());
  }
  public pMOEAD(Problem problem, OrderedLock P) {
    super(problem);
  _$lock_P = P;
    parentThread_ = null;
    functionType_ = "_TCHE1";
  }
  public pMOEAD(pMOEAD parentThread, Problem problem, int id, int numberOfThreads) {
  this(parentThread, problem, id, numberOfThreads, new OrderedLock());
  }
  public pMOEAD(pMOEAD parentThread, Problem problem, int id, int numberOfThreads, OrderedLock P) {
    super(problem);
  _$lock_P = P;
    parentThread_ = parentThread;
    functionType_ = "_TCHE1";
  }
  private void parallel_execution(int i, double delta, int partitions){
    int n;
    int type;
    double rnd;
    Vector<Integer> p;
    Solution child;
    Solution[] parents;
  {
    n = i;
    rnd = PseudoRandom.randDouble();
    if(rnd < delta) {
      type = 1;
    }
    else {
      type = 2;
    }
    p = new Vector<Integer>();
    synchronized(_$lock_P) {
    this.matingSelection_internal(p, n, 2, type);
    child = null;
    parents = new Solution[3];
    try {
      parents[0] = parentThread_.population_.get(p.get(0));
      parents[1] = parentThread_.population_.get(p.get(1));
      parents[2] = parentThread_.population_.get(n);
      child = (Solution)parentThread_.crossover_.execute(new Object[]{ parentThread_.population_.get(n), parents } );
      mutation_.execute(child);
      problem_.evaluate(child);
    }
    catch (JMException ex) {
      Logger.getLogger(pMOEAD.class.getName()).log(Level.SEVERE, null, ex);
    }
    updateReference_internal(child);
    updateOfSolutions_internal(child, n, type);
  }
    }
  }
  private void parallel_execution_internal(int i, double delta, int partitions){
    int n = i;
    int type;
    double rnd = PseudoRandom.randDouble();
    if(rnd < delta) {
      type = 1;
    }
    else {
      type = 2;
    }
    Vector<Integer> p = new Vector<Integer>();
    this.matingSelection_internal(p, n, 2, type);
    Solution child = null;
    Solution[] parents = new Solution[3];
    try {
      parents[0] = parentThread_.population_.get(p.get(0));
      parents[1] = parentThread_.population_.get(p.get(1));
      parents[2] = parentThread_.population_.get(n);
      child = (Solution)parentThread_.crossover_.execute(new Object[]{ parentThread_.population_.get(n), parents } );
      mutation_.execute(child);
      problem_.evaluate(child);
    }
    catch (JMException ex) {
      Logger.getLogger(pMOEAD.class.getName()).log(Level.SEVERE, null, ex);
    }
    updateReference_internal(child);
    updateOfSolutions_internal(child, n, type);
  }
  public void run(){
    int partitions;
    double delta;
    long estimatedTime;
  {
    synchronized(_$lock_P) {
    neighborhood_ = parentThread_.neighborhood_;
    problem_ = parentThread_.problem_;
    lambda_ = parentThread_.lambda_;
    population_ = parentThread_.population_;
    z_ = parentThread_.z_;
    indArray_ = parentThread_.indArray_;
    partitions = parentThread_.populationSize_;
    maxEvaluations_ = parentThread_.maxEvaluations_;
    delta = parentThread_.delta_;
    }
    evaluations_ = 0;
    do {{
        try {
        _dcFor$0(0, partitions, 1, partitions, delta);
        org.jikesrvm.scheduler.WS.finish();
        } catch(org.jikesrvm.scheduler.WS.Finish _$$f) {
        }
      }
      evaluations_ += partitions;
    }while(evaluations_ < maxEvaluations_);
    synchronized(_$lock_P) {
    estimatedTime = System.currentTimeMillis() - parentThread_.initTime_;
    }
    System.out.println("Time : " + estimatedTime);
  }
  }
        public  void _dcFor$0(int _$lower, int _$upper, int _$sliceNum, int partitions, double delta) {
        final int procs = org.jikesrvm.scheduler.WS.wsProcs;
        if (_$sliceNum >> 2 < procs) {
        int var0 = _$lower + _$upper >> 1;
        int var1 = _$sliceNum << 1;
        try {
        org.jikesrvm.scheduler.WS.setFlag();
        _dcFor$0(_$lower, var0, var1, partitions, delta);
        org.jikesrvm.scheduler.WS.join();
        } catch (org.jikesrvm.scheduler.WS.Continuation c) {}
        _dcFor$0(var0, _$upper, var1, partitions, delta);
        } else {
        for (int i=_$lower; i<_$upper; i++) {
        {
            parallel_execution(i, delta, partitions);
          }
        }
        }
        }
  public SolutionSet execute() throws JMException, ClassNotFoundException{
    synchronized(_$lock_P) {
    parentThread_ = this;
    evaluations_ = 0;
    maxEvaluations_ = ((Integer)this.getInputParameter("maxEvaluations")).intValue();
    populationSize_ = ((Integer)this.getInputParameter("populationSize")).intValue();
    dataDirectory_ = this.getInputParameter("dataDirectory").toString();
    population_ = new SolutionSet(populationSize_);
    indArray_ = new Solution[problem_.getNumberOfObjectives()];
    T_ = 20;
    delta_ = 0.9D;
    nr_ = 2;
    neighborhood_ = new int[populationSize_][T_];
    z_ = new double[problem_.getNumberOfObjectives()];
    lambda_ = new double[populationSize_][problem_.getNumberOfObjectives()];
    crossover_ = operators_.get("crossover");
    mutation_ = operators_.get("mutation");
    }
    initUniformWeight_internal();
    initNeighborhood_internal();
    initPopulation();
    initIdealPoint();
    initTime_ = System.currentTimeMillis();
    run();
    return population_;
  }
  public void initNeighborhood(){
    double[] x;
    int[] idx;
  {
    x = new double[populationSize_];
    idx = new int[populationSize_];
    for(int i = 0; i < populationSize_; i++) {
      for(int j = 0; j < populationSize_; j++) {
        x[j] = Utils.distVector(lambda_[i], lambda_[j]);
        idx[j] = j;
      }
      Utils.minFastSort(x, idx, populationSize_, T_);
      for(int k = 0; k < T_; k++) {
        neighborhood_[i][k] = idx[k];
      }
    }
  }
  }
  public void initNeighborhood_internal(){
    double[] x = new double[populationSize_];
    int[] idx = new int[populationSize_];
    for(int i = 0; i < populationSize_; i++) {
      for(int j = 0; j < populationSize_; j++) {
        x[j] = Utils.distVector(lambda_[i], lambda_[j]);
        idx[j] = j;
      }
      Utils.minFastSort(x, idx, populationSize_, T_);
      for(int k = 0; k < T_; k++) {
        neighborhood_[i][k] = idx[k];
      }
    }
  }
  public void initUniformWeight(){
        double a;
      String dataFileName;
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        int numberOfObjectives;
        int i;
        int j;
        String aux;
          StringTokenizer st;
            double value;
  {
    if((problem_.getNumberOfObjectives() == 2) && (populationSize_ < 300)) {
      for(int n = 0; n < populationSize_; n++) {
        a = 1.0D * n / (populationSize_ - 1);
        lambda_[n][0] = a;
        lambda_[n][1] = 1 - a;
      }
    }
    else {
      dataFileName = "W" + problem_.getNumberOfObjectives() + "D_" + populationSize_ + ".dat";
      System.out.println(dataDirectory_);
      System.out.println(dataDirectory_ + "/" + dataFileName);
      try {
        fis = new FileInputStream(dataDirectory_ + "/" + dataFileName);
        isr = new InputStreamReader(fis);
        br = new BufferedReader(isr);
        numberOfObjectives = 0;
        i = 0;
        j = 0;
        aux = br.readLine();
        while(aux != null){
          st = new StringTokenizer(aux);
          j = 0;
          numberOfObjectives = st.countTokens();
          while(st.hasMoreTokens()){
            value = (new Double(st.nextToken())).doubleValue();
            lambda_[i][j] = value;
            j++;
          }
          aux = br.readLine();
          i++;
        }
        br.close();
      }
      catch (Exception e) {
        System.out.println("initUniformWeight: fail when reading for file: " + dataDirectory_ + "/" + dataFileName);
        e.printStackTrace();
      }
    }
  }
  }
  public void initUniformWeight_internal(){
    if((problem_.getNumberOfObjectives() == 2) && (populationSize_ < 300)) {
      for(int n = 0; n < populationSize_; n++) {
        double a = 1.0D * n / (populationSize_ - 1);
        lambda_[n][0] = a;
        lambda_[n][1] = 1 - a;
      }
    }
    else {
      String dataFileName;
      dataFileName = "W" + problem_.getNumberOfObjectives() + "D_" + populationSize_ + ".dat";
      System.out.println(dataDirectory_);
      System.out.println(dataDirectory_ + "/" + dataFileName);
      try {
        FileInputStream fis = new FileInputStream(dataDirectory_ + "/" + dataFileName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        int numberOfObjectives = 0;
        int i = 0;
        int j = 0;
        String aux = br.readLine();
        while(aux != null){
          StringTokenizer st = new StringTokenizer(aux);
          j = 0;
          numberOfObjectives = st.countTokens();
          while(st.hasMoreTokens()){
            double value = (new Double(st.nextToken())).doubleValue();
            lambda_[i][j] = value;
            j++;
          }
          aux = br.readLine();
          i++;
        }
        br.close();
      }
      catch (Exception e) {
        System.out.println("initUniformWeight: fail when reading for file: " + dataDirectory_ + "/" + dataFileName);
        e.printStackTrace();
      }
    }
  }
  public void initPopulation() throws JMException, ClassNotFoundException{
      Solution newSolution;
  {
    for(int i = 0; i < populationSize_; i++) {
      newSolution = new Solution(problem_);
      evaluations_++;
      population_.add(newSolution);
    }{
      try {
      _dcFor$1(0, populationSize_, 1, populationSize_, population_, problem_);
      org.jikesrvm.scheduler.WS.finish();
      } catch(org.jikesrvm.scheduler.WS.Finish _$$f) {
      }
    }
  }
  }
      public  void _dcFor$1(int _$lower, int _$upper, int _$sliceNum, int populationSize_, jmetal.core.SolutionSet population_, jmetal.core.Problem problem_) throws jmetal.util.JMException {
      final int procs = org.jikesrvm.scheduler.WS.wsProcs;
      if (_$sliceNum >> 2 < procs) {
      int var0 = _$lower + _$upper >> 1;
      int var1 = _$sliceNum << 1;
      try {
      org.jikesrvm.scheduler.WS.setFlag();
      _dcFor$1(_$lower, var0, var1, populationSize_, population_, problem_);
      org.jikesrvm.scheduler.WS.join();
      } catch (org.jikesrvm.scheduler.WS.Continuation c) {}
      _dcFor$1(var0, _$upper, var1, populationSize_, population_, problem_);
      } else {
      for (int i=_$lower; i<_$upper; i++) {
      {
          Solution newSolution = population_.get(i);
          problem_.evaluate(newSolution);
        }
      }
      }
      }
  void initIdealPoint() throws JMException, ClassNotFoundException{{
      try {
      _dcFor$2(0, problem_.getNumberOfObjectives(), 1, problem_, z_, indArray_);
      org.jikesrvm.scheduler.WS.finish();
      } catch(org.jikesrvm.scheduler.WS.Finish _$$f) {
      }
    }
    evaluations_ += problem_.getNumberOfObjectives();{
      try {
      _dcFor$3(0, populationSize_, 1, populationSize_, population_);
      org.jikesrvm.scheduler.WS.finish();
      } catch(org.jikesrvm.scheduler.WS.Finish _$$f) {
      }
    }
  }
       void _dcFor$2(int _$lower, int _$upper, int _$sliceNum, jmetal.core.Problem problem_, double[] z_, jmetal.core.Solution[] indArray_) throws jmetal.util.JMException, ClassNotFoundException {
      final int procs = org.jikesrvm.scheduler.WS.wsProcs;
      if (_$sliceNum >> 2 < procs) {
      int var0 = _$lower + _$upper >> 1;
      int var1 = _$sliceNum << 1;
      try {
      org.jikesrvm.scheduler.WS.setFlag();
      _dcFor$2(_$lower, var0, var1, problem_, z_, indArray_);
      org.jikesrvm.scheduler.WS.join();
      } catch (org.jikesrvm.scheduler.WS.Continuation c) {}
      _dcFor$2(var0, _$upper, var1, problem_, z_, indArray_);
      } else {
      for (int i=_$lower; i<_$upper; i++) {
      {
          z_[i] = 1.0e+30D;
          indArray_[i] = new Solution(problem_);
          problem_.evaluate(indArray_[i]);
        }
      }
      }
      }
       void _dcFor$3(int _$lower, int _$upper, int _$sliceNum, int populationSize_, jmetal.core.SolutionSet population_) {
      final int procs = org.jikesrvm.scheduler.WS.wsProcs;
      if (_$sliceNum >> 2 < procs) {
      int var0 = _$lower + _$upper >> 1;
      int var1 = _$sliceNum << 1;
      try {
      org.jikesrvm.scheduler.WS.setFlag();
      _dcFor$3(_$lower, var0, var1, populationSize_, population_);
      org.jikesrvm.scheduler.WS.join();
      } catch (org.jikesrvm.scheduler.WS.Continuation c) {}
      _dcFor$3(var0, _$upper, var1, populationSize_, population_);
      } else {
      for (int i=_$lower; i<_$upper; i++) {
      {
          updateReference(population_.get(i));
        }
      }
      }
      }
  public void matingSelection(Vector<Integer> list, int cid, int size, int type){
    int ss;
    int r;
    int p;
      boolean flag;
  {
    synchronized(_$lock_P) {
    ss = parentThread_.neighborhood_[cid].length;
    while(list.size() < size){
      if(type == 1) {
        r = PseudoRandom.randInt(0, ss - 1);
        p = parentThread_.neighborhood_[cid][r];
      }
      else {
        p = PseudoRandom.randInt(0, parentThread_.populationSize_ - 1);
      }
      flag = true;
      for(int i = 0; i < list.size(); i++) {
        if(list.get(i) == p) {
          flag = false;
          break ;
        }
      }
      if(flag) {
        list.addElement(p);
      }
    }
  }
    }
  }
  public void matingSelection_internal(Vector<Integer> list, int cid, int size, int type){
    int ss;
    int r;
    int p;
    ss = parentThread_.neighborhood_[cid].length;
    while(list.size() < size){
      if(type == 1) {
        r = PseudoRandom.randInt(0, ss - 1);
        p = parentThread_.neighborhood_[cid][r];
      }
      else {
        p = PseudoRandom.randInt(0, parentThread_.populationSize_ - 1);
      }
      boolean flag = true;
      for(int i = 0; i < list.size(); i++) {
        if(list.get(i) == p) {
          flag = false;
          break ;
        }
      }
      if(flag) {
        list.addElement(p);
      }
    }
  }
  void updateReference(Solution individual){
    synchronized(_$lock_P) {
    for(int n = 0; n < parentThread_.problem_.getNumberOfObjectives(); n++) {
      if(individual.getObjective(n) < z_[n]) {
        parentThread_.z_[n] = individual.getObjective(n);
        parentThread_.indArray_[n] = individual;
      }
    }
  }
    }
  void updateReference_internal(Solution individual){
    for(int n = 0; n < parentThread_.problem_.getNumberOfObjectives(); n++) {
      if(individual.getObjective(n) < z_[n]) {
        parentThread_.z_[n] = individual.getObjective(n);
        parentThread_.indArray_[n] = individual;
      }
    }
  }
  void updateOfSolutions(Solution indiv, int id, int type){
    int size;
    int time;
    int[] perm;
      int k;
      double f1;
      double f2;
  {
    time = 0;
    synchronized(_$lock_P) {
    if(type == 1) {
      size = parentThread_.neighborhood_[id].length;
    }
    else {
      size = parentThread_.population_.size();
    }
    perm = new int[size];
    Utils.randomPermutation(perm, size);
    for(int i = 0; i < size; i++) {
      if(type == 1) {
        k = parentThread_.neighborhood_[id][perm[i]];
      }
      else {
        k = perm[i];
      }
      f2 = fitnessFunction_internal(indiv, parentThread_.lambda_[k]);
      f1 = fitnessFunction_internal(parentThread_.population_.get(k), parentThread_.lambda_[k]);
      if(f2 < f1) {
        parentThread_.population_.replace(k, new Solution(indiv));
        time++;
      }
      if(time >= parentThread_.nr_) {
        return ;
      }
    }
  }
    }
  }
  void updateOfSolutions_internal(Solution indiv, int id, int type){
    int size;
    int time;
    time = 0;
    if(type == 1) {
      size = parentThread_.neighborhood_[id].length;
    }
    else {
      size = parentThread_.population_.size();
    }
    int[] perm = new int[size];
    Utils.randomPermutation(perm, size);
    for(int i = 0; i < size; i++) {
      int k;
      if(type == 1) {
        k = parentThread_.neighborhood_[id][perm[i]];
      }
      else {
        k = perm[i];
      }
      double f1;
      double f2;
      f2 = fitnessFunction_internal(indiv, parentThread_.lambda_[k]);
      f1 = fitnessFunction_internal(parentThread_.population_.get(k), parentThread_.lambda_[k]);
      if(f2 < f1) {
        parentThread_.population_.replace(k, new Solution(indiv));
        time++;
      }
      if(time >= parentThread_.nr_) {
        return ;
      }
    }
  }
  double fitnessFunction(Solution individual, double[] lambda){
    double fitness;
      double maxFun;
        double diff;
        double feval;
  {
    fitness = 0.0D;
    synchronized(_$lock_P) {
    if(parentThread_.functionType_.equals("_TCHE1")) {
      maxFun = -1.0e+30D;
      for(int n = 0; n < parentThread_.problem_.getNumberOfObjectives(); n++) {
        diff = Math.abs(individual.getObjective(n) - z_[n]);
        if(lambda[n] == 0) {
          feval = 0.0001D * diff;
        }
        else {
          feval = diff * lambda[n];
        }
        if(feval > maxFun) {
          maxFun = feval;
        }
      }
      fitness = maxFun;
    }
    else {
      System.out.println("MOEAD.fitnessFunction: unknown type " + functionType_);
      System.exit(-1);
    }
    }
    return fitness;
  }
  }
  double fitnessFunction_internal(Solution individual, double[] lambda){
    double fitness;
    fitness = 0.0D;
    if(parentThread_.functionType_.equals("_TCHE1")) {
      double maxFun = -1.0e+30D;
      for(int n = 0; n < parentThread_.problem_.getNumberOfObjectives(); n++) {
        double diff = Math.abs(individual.getObjective(n) - z_[n]);
        double feval;
        if(lambda[n] == 0) {
          feval = 0.0001D * diff;
        }
        else {
          feval = diff * lambda[n];
        }
        if(feval > maxFun) {
          maxFun = feval;
        }
      }
      fitness = maxFun;
    }
    else {
      System.out.println("MOEAD.fitnessFunction: unknown type " + functionType_);
      System.exit(-1);
    }
    return fitness;
  }
}
