package org.lpw.clivia.shortcut;

public interface ShortcutService {
    /**
     * 编码是否存在验证器Bean名称。
     */
    String VALIDATOR_CODE_EXISTS = ShortcutModel.NAME + ".validator.code.exists";

    /**
     * 查找值，如果不存在则返回空字符串。
     *
     * @param code 编码。
     * @return 值。
     */
    String find(String code);

    /**
     * 保存。
     *
     * @param length 编码长度。
     * @param value  值。
     * @return 编码。
     */
    String save(int length, String value);
}
