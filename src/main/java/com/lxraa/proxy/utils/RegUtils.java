package com.lxraa.proxy.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegUtils {
    public static List<String> matchAll(String content,Pattern pattern){
        List<String> list = new ArrayList<>();
        Matcher matcher = pattern.matcher(content);
        while(matcher.find()){
            list.add(matcher.group());
        }
        return list;
    }
}
