package com.example.myapplication.util;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ExecutorsProvider {
    private static final Executor IO = Executors.newSingleThreadExecutor();
    public static Executor io() { return IO; }
}