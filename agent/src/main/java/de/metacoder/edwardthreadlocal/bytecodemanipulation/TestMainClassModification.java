package de.metacoder.edwardthreadlocal.bytecodemanipulation;

import de.metacoder.edwardthreadlocal.org.objectweb.asm.ClassVisitor;
import de.metacoder.edwardthreadlocal.org.objectweb.asm.Opcodes;
import de.metacoder.edwardthreadlocal.util.InstrumentationUtils;

import java.lang.instrument.Instrumentation;
import java.util.function.Function;

import static de.metacoder.edwardthreadlocal.util.logging.Log.fine;
import static de.metacoder.edwardthreadlocal.util.logging.Log.infoAround;

public class TestMainClassModification {
  public static void apply(Instrumentation inst) {
    inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
      if("de/metacoder/edwardthreadlocal/test/Main".equals(className)) {
        return infoAround("TestMainClassModification.modifyTestMainBytecode", () -> modifyTestMainBytecode(classfileBuffer));
      } else {
        fine("<TestMainClassModification> Skipping " + className);
        return classfileBuffer;
      }
    });
  }

  private static byte[] modifyTestMainBytecode(byte[] testMainBytecode) {
    return InstrumentationUtils.modifyBytecode(testMainBytecode, MODIFY_TEST_MAIN_CLASS);
  }

  private static final Function<ClassVisitor, ClassVisitor> MODIFY_AFTER_BL_METHOD =
    cv -> new MethodConsumingClassVisitor("afterBL", "change-TestMain-beforeBL", mv ->
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "de/metacoder/edwardthreadlocal/TraceReceiver", "deactivateTracingForThread", "()V", false)
      , cv);

  private static final Function<ClassVisitor, ClassVisitor> MODIFY_BEFORE_BL_METHOD =
    cv -> new MethodConsumingClassVisitor("beforeBL", "change-TestMain-beforeBL", mv ->
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "de/metacoder/edwardthreadlocal/TraceReceiver", "activateTracingForThread", "()V", false)
      , cv);

  private static final Function<ClassVisitor, ClassVisitor> MODIFY_TEST_MAIN_CLASS =
    MODIFY_AFTER_BL_METHOD.andThen(MODIFY_BEFORE_BL_METHOD);
}
