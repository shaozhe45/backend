package com.changqing.gov.order.enums;

import com.changqing.gov.base.BaseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

/**
 * <p>
 * 实体注释中生成的类型枚举
 * 用户
 * </p>
 *
 * @author changqing
 * @date 2020-02-14
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Sex", description = "性别-枚举")
public enum SexTest implements BaseEnum {

    /**
     * W="女"
     */
    W("女"),
    /**
     * M="男"
     */
    M("男"),
    /**
     * N="未知"
     */
    N("未知"),
    ;

    @ApiModelProperty(value = "描述")
    private String desc;

    public static SexTest match(String val, SexTest def) {
        return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static SexTest matchDesc(String val, SexTest def) {
        return Stream.of(values()).parallel().filter((item) -> item.getDesc().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static SexTest get(String val) {
        return match(val, null);
    }

    public boolean eq(SexTest val) {
        return val == null ? false : eq(val.name());
    }

    @Override
    @ApiModelProperty(value = "编码", allowableValues = "W,M,N", example = "W")
    public String getCode() {
        return this.name();
    }

}
