package test;

// Testing inference for composite declarations
public class Test07 {

  Test07 f1 = new Test07(), f2 = new Test07(), f3 = new Test07(); // @NonNull
  Test07 f4 = new Test07(), f5 = null; // One part is nullable -> The whole decl will be nullable
  Test07 f6 = null, f7 = new Test07(); // One part is nullable -> The whole decl will be nullable

  public void test() {
    Test07 v1 = new Test07(), v2 = new Test07(), v3 = new Test07(); // @NonNull
    Test07 v4 = new Test07(), v5 = null; // One part is nullable -> The whole decl will be nullable
    Test07 v6 = null, v7 = new Test07(); // One part is nullable -> The whole decl will be nullable
  }

}
