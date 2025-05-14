package org.demo;

import org.openrewrite.ExecutionContext;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.RecipeRun;
import org.openrewrite.Result;
import org.openrewrite.SourceFile;
import org.openrewrite.internal.InMemoryLargeSourceSet;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.tree.J;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CheckstyleFixPoc {

    private static final Logger logger = Logger.getLogger(CheckstyleFixPoc.class.getName());

    public static void main(String... args) throws Exception {
        // Path to the existing test file
        Path testFile = Paths.get("src/main/java/org/demo/Test.java");
        logger.info("Using test file: " + testFile);

        // Fix the file and overwrite the existing file with the changes
        fixLowercaseLongLiterals(testFile);
    }

    public static void fixLowercaseLongLiterals(Path filePath) {
        boolean fixed = false;

        try {
            ExecutionContext ctx = new InMemoryExecutionContext(Throwable::printStackTrace);
            List<J.CompilationUnit> cus = JavaParser.fromJavaVersion().build()
                .parse(Collections.singletonList(filePath), null, ctx)
                .filter(sf -> sf instanceof J.CompilationUnit)
                .map(sf -> (J.CompilationUnit) sf)
                .collect(Collectors.toList());

            if (!cus.isEmpty()) {

                Set<String> toReplace = new HashSet<>();
                for (J.CompilationUnit cu : cus) {
                    new JavaIsoVisitor<ExecutionContext>() {
                        @Override
                        public J.Literal visitLiteral(J.Literal literal, ExecutionContext ctx) {
                            String valueSource = literal.getValueSource();
                            if (valueSource != null && valueSource.matches("\\d+l")) {
                                toReplace.add(valueSource);
                            }
                            return super.visitLiteral(literal, ctx);
                        }
                    }.visit(cu, ctx);
                }

                if (!toReplace.isEmpty()) {
                    List<SourceFile> sourceFiles = new ArrayList<>(cus);
                    InMemoryLargeSourceSet sourceSet = new InMemoryLargeSourceSet(sourceFiles);

                    RecipeRun run = new LiteralFixer(toReplace).run(sourceSet, ctx);
                    List<Result> results = run.getChangeset().getAllResults();
                    if (!results.isEmpty()) {
                        // Overwrite the file with the fixed content
                        Files.writeString(filePath, results.get(0).getAfter().printAll());
                        fixed = true;
                    }
                }
            }
        } catch (Exception e) {
            logger.severe("Error occurred while fixing lowercase long literals: " + e.getMessage());
        } finally {
            if (fixed) {
                logger.info("File successfully updated with fixes.");
            } else {
                logger.info("No violations fixed.");
            }
        }
    }

    public static class LiteralFixer extends org.openrewrite.Recipe {
        private static final Pattern L_$ = Pattern.compile("l$");
        private final Set<String> targets;

        public LiteralFixer(Set<String> targets) {
            this.targets = targets;
        }

        @Override
        public String getDisplayName() {
            return "Fix lowercase 'l' long literals";
        }

        @Override
        public String getDescription() {
            return "Replaces lowercase 'l' with uppercase 'L' in long literals.";
        }

        @Override
        public JavaIsoVisitor<ExecutionContext> getVisitor() {
            return new JavaIsoVisitor<>() {
                @Override
                public J.Literal visitLiteral(J.Literal literal, ExecutionContext ctx) {
                    J.Literal l = super.visitLiteral(literal, ctx);
                    String valueSource = l.getValueSource();
                    J.Literal result = l;  // Default to the original literal

                    if (valueSource != null && targets.contains(valueSource)) {
                        result = l.withValueSource(L_$.matcher(valueSource).replaceAll("L"));
                    }

                    return result;
                }
            };
        }
    }
}
