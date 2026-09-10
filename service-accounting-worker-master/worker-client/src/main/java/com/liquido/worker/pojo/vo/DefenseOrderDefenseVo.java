package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefenseOrderDefenseVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(1)
    private Long id;

    @NotBlank
    @Length(max = 400)
    private String defenseDescription;

    private List<Long> defenseAppendixIds;

}
