package com.example.activiti_demo.entity;
 
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
 
import java.io.Serializable;
import java.time.LocalDateTime;
 
/**
 * @author ningbeibei
 * @since 2024-09-26
 */
// 第六次提交
@Getter
@Setter
@TableName("user")
public class UserEntity implements Serializable {
 
    private static final long serialVersionUID = 1L;
 
    @TableField("ID")
    private Integer id;
 
    @TableField("NAME")
    private String name;
 
    @TableField("AGE")
    private int age;
 
    @TableField("CREATED_BY")
    private String createdBy;
 
    @TableField("CREATED_TIME")
    private LocalDateTime createdTime;
 
    @TableField("CREATED_ID")
    private String createdId;
 
    @TableField("UPDATED_BY")
    private String updatedBy;
 
    @TableField("UPDATED_TIME")
    private LocalDateTime updatedTime;
 
    @TableField("UPDATED_ID")
    private String updatedId;
 
 
}