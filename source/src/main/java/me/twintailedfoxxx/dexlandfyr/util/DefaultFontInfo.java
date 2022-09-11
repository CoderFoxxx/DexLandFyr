package me.twintailedfoxxx.dexlandfyr.util;

public enum DefaultFontInfo {
    A('A', 5),
    a('a', 5),
    B('B', 5),
    b('b', 5),
    C('C', 5),
    c('c', 5),
    D('D', 5),
    d('d', 5),
    E('E', 5),
    e('e', 5),
    F('F', 5),
    f('f', 4),
    G('G', 5),
    g('g', 5),
    H('H', 5),
    h('h', 5),
    I('I', 3),
    i('i', 1),
    J('J', 5),
    j('j', 5),
    K('K', 5),
    k('k', 4),
    L('L', 5),
    l('l', 1),
    M('M', 5),
    m('m', 5),
    N('N', 5),
    n('n', 5),
    O('O', 5),
    o('o', 5),
    P('P', 5),
    p('p', 5),
    Q('Q', 5),
    q('q', 5),
    R('R', 5),
    r('r', 5),
    S('S', 5),
    s('s', 5),
    T('T', 5),
    t('t', 4),
    U('U', 5),
    u('u', 5),
    V('V', 5),
    v('v', 5),
    W('W', 5),
    w('w', 5),
    X('X', 5),
    x('x', 5),
    Y('Y', 5),
    y('y', 5),
    Z('Z', 5),
    z('z', 5),
    RUS_0('А', 5),
    RUS_1('Б', 5),
    RUS_2('В', 5),
    RUS_3('Г', 5),
    RUS_4('Д', 6),
    RUS_5('Е', 5),
    RUS_31('Ё', 6),
    RUS_6('Ж', 7),
    RUS_7('З', 5),
    RUS_8('И', 5),
    RUS_9('Й', 5),
    RUS_10('К', 5),
    RUS_11('Л', 5),
    RUS_12('М', 5),
    RUS_13('Н', 5),
    RUS_14('О', 5),
    RUS_15('П', 5),
    RUS_16('Р', 5),
    RUS_17('С', 5),
    RUS_18('Т', 5),
    RUS_19('У', 5),
    RUS_20('Ф', 7),
    RUS_21('Х', 5),
    RUS_22('Ц', 6),
    RUS_23('Ч', 5),
    RUS_24('Ш', 7),
    RUS_25('Щ', 8),
    RUS_26('Ъ', 6),
    RUS_27('Ь', 6),
    RUS_32('Ы', 7),
    RUS_28('Э', 5),
    RUS_29('Ю', 7),
    RUS_30('Я', 5),
    LOW_RUS_1('а', 5),
    LOW_RUS_2('б', 5),
    LOW_RUS_3('в', 5),
    LOW_RUS_4('г', 4),
    LOW_RUS_5('д', 6),
    LOW_RUS_6('е', 5),
    LOW_RUS_7('ё', 5),
    LOW_RUS_8('ж', 5),
    LOW_RUS_9('з', 5),
    LOW_RUS_10('и', 5),
    LOW_RUS_11('й', 5),
    LOW_RUS_12('к', 4),
    LOW_RUS_13('л', 5),
    LOW_RUS_14('м', 5),
    LOW_RUS_15('н', 5),
    LOW_RUS_16('о', 5),
    LOW_RUS_17('п', 5),
    LOW_RUS_18('р', 5),
    LOW_RUS_19('с', 5),
    LOW_RUS_20('т', 5),
    LOW_RUS_21('у', 5),
    LOW_RUS_22('ф', 5),
    LOW_RUS_23('х', 5),
    LOW_RUS_24('ц', 5),
    LOW_RUS_25('ч', 5),
    LOW_RUS_26('ш', 5),
    LOW_RUS_27('щ', 6),
    LOW_RUS_28('ъ', 6),
    LOW_RUS_29('ы', 6),
    LOW_RUS_30('ь', 5),
    LOW_RUS_31('э', 5),
    LOW_RUS_32('ю', 7),
    LOW_RUS_33('я', 5),
    NUM_1('1', 5),
    NUM_2('2', 5),
    NUM_3('3', 5),
    NUM_4('4', 5),
    NUM_5('5', 5),
    NUM_6('6', 5),
    NUM_7('7', 5),
    NUM_8('8', 5),
    NUM_9('9', 5),
    NUM_0('0', 5),
    EXCLAMATION_POINT('!', 1),
    AT_SYMBOL('@', 6),
    NUM_SIGN('#', 5),
    DOLLAR_SIGN('$', 5),
    PERCENT('%', 5),
    UP_ARROW('^', 5),
    AMPERSAND('&', 5),
    ASTERISK('*', 5),
    LEFT_PARENTHESIS('(', 4),
    RIGHT_PERENTHESIS(')', 4),
    MINUS('-', 5),
    UNDERSCORE('_', 5),
    PLUS_SIGN('+', 5),
    EQUALS_SIGN('=', 5),
    LEFT_CURL_BRACE('{', 4),
    RIGHT_CURL_BRACE('}', 4),
    LEFT_BRACKET('[', 3),
    RIGHT_BRACKET(']', 3),
    COLON(':', 1),
    SEMI_COLON(';', 1),
    DOUBLE_QUOTE('"', 3),
    SINGLE_QUOTE('\'', 1),
    LEFT_ARROW('<', 4),
    RIGHT_ARROW('>', 4),
    QUESTION_MARK('?', 5),
    SLASH('/', 5),
    BACK_SLASH('\\', 5),
    LINE('|', 1),
    TILDE('~', 5),
    TICK('`', 2),
    PERIOD('.', 1),
    COMMA(',', 1),
    SPACE(' ', 3),
    DEFAULT('a', 4);

    private final char character;
    private final int length;

    DefaultFontInfo(char character, int length) {
        this.character = character;
        this.length = length;
    }

    public static DefaultFontInfo getDefaultFontInfo(char c) {
        for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
            if (dFI.getCharacter() == c) return dFI;
        }
        return DefaultFontInfo.DEFAULT;
    }

    public char getCharacter() {
        return this.character;
    }

    public int getLength() {
        return this.length;
    }

    public int getBoldLength() {
        if (this == DefaultFontInfo.SPACE) return this.getLength();
        return this.length + 1;
    }
}