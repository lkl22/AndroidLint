package com.lkl.lint.demo.inherit;


class InheritTest extends SuperClass implements Interface {
    @Override
    public void test() {
        setInterface(new Interface() {
            @Override
            public void test() {

            }
        });

        setInterface(() -> {

        });
    }

    public void setInterface(Interface i) {

    }
}
