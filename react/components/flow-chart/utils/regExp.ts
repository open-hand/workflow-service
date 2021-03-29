/**
 * 验证 编码 可输入字符限制
 * 提示：intl.get('hzero.common.validation.codeLower').d('全小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”')
 */
export const CODE_LOWER = /^[a-zA-Z0-9][a-zA-Z0-9_.]*$/;
