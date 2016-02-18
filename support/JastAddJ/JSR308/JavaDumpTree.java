import AST.*;

class JavaDumpTree extends Frontend {
  public static void main(String args[]) {
    if(!compile(args))
      System.exit(1);
  }

  public static boolean compile(String args[]) {
    return new JavaDumpTree().process(
        args,
        new BytecodeParser(),
        new JavaParser() {
          public CompilationUnit parse(java.io.InputStream is, String fileName) throws java.io.IOException, beaver.Parser.Exception {
            return new parser.JavaParser().parse(is, fileName);
          }
        }
    );
  }
  protected void processErrors(java.util.Collection errors, CompilationUnit unit) {
    super.processErrors(errors, unit);
    System.out.println(unit.dumpTreeNoRewrite());
  }
  protected void processNoErrors(CompilationUnit unit) {
    System.out.println(unit.dumpTreeNoRewrite());
  }

  protected String name() { return "Java1.4Frontend + Backend + Java5Extensions Dumptree"; }
  protected String version() { return "R20071015"; }
}
