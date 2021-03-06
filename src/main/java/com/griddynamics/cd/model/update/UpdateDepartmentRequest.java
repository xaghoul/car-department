package com.griddynamics.cd.model.update;

import com.griddynamics.cd.model.DepartmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDepartmentRequest {

    @Pattern(regexp = "^[a-zA-Z0-9\s]+", message = "Must contain only letters")
    private String name;
    @Email(message = "Email should be valid")
    private String email;
    private String description;
    private DepartmentType departmentType;
}
