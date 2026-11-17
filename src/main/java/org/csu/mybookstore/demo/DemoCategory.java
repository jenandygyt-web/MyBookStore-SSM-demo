package org.csu.mybookstore.demo;

import lombok.Data;

/** 演示用图书分类。 */
@Data
public class DemoCategory {

    private String code;
    private String name;
    private String en;
    private String mark;
    private String desc;
}
