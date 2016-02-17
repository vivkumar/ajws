package test;

public class Test06 {
  // Testing inference for composite declarations.
  // Exposes deficiency in current inference implementation.
  // If one of the variables in a composite is non-null, none of the others
  // in the composite can be annotated, since the annotation is on the whole
  // composite.
  // If annotations on individuals in a composite declaration are introduced,
  // it would be natural to fix this deficiency.
  void test(boolean condition) {
    // A composite declaration with two non-null and one nullable.
    Test06 l1 = new Test06(), l2 = new Test06(), l3 = null; // The whole decl cannot be marked NonNull.
    // Unfortunately, no propagation to uses of the nonnull parts:
    Test06 l4 = l1; // Will unfortunately not be marked @NonNull.
  }

}
