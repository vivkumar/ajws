import AST.*;

class JavaChecker extends Frontend {

  public static void main(String args[]) {
    compile(args);
  }

  public static boolean compile(String args[]) {
    return new JavaChecker().process(
        args,
        new BytecodeParser(),
        new JavaParser() {
          public CompilationUnit parse(java.io.InputStream is, String fileName) throws java.io.IOException, beaver.Parser.Exception {
            return new parser.JavaParser().parse(is, fileName);
          }
        }
    );
  }

  protected String name() { return "JavaChecker with NonNullTypes"; }
  protected String version() { return "R20080305"; }
}
