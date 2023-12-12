package me.ludvick.brisk.walker.bots.telegram.constants;

public enum Condition {
    GREETING(1, "greeting"),
    ERROR(2, "error"),
    MAIN_MENU(3, "main_menu"),
    SUB_MENU(4, "sub_menu");

    Condition(int id, String message) {

    }
}
