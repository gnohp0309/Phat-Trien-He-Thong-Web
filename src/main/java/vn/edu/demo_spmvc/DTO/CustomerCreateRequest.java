package vn.edu.demo_spmvc.DTO;

import jakarta.validation.constraints.NotBlank;

public class CustomerCreateRequest {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

