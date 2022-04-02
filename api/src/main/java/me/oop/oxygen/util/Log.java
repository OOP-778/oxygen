package me.oop.oxygen.util;

import java.lang.StackWalker.Option;

public class Log {

    public static void log(String message, Object... args) {
        final String format = String.format("<%s>: ", getCaller());
        System.out.println(String.format(String.format("%s%s", format, String.format(message, args))));
    }

    public static String getCaller() {
        return StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)
            .walk(frames -> frames.skip(2).map(frame -> String.format("%s:%s", frame.getDeclaringClass().getSimpleName(), frame.getMethodName()))
                .findFirst()
                .orElseThrow());
    }

}
