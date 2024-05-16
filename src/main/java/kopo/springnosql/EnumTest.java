package kopo.springnosql;

import lombok.RequiredArgsConstructor;

public class EnumTest {
    @RequiredArgsConstructor
    public enum HandlingURL {
        BASE("/redis/v1"),
        SAVE_HASH("saveHash"),
        ;

        public  final String path;

    }
    public static void main(String[] args) {

    }
}
