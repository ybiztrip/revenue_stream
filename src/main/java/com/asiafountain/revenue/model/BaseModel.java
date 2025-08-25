package com.asiafountain.revenue.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Setter
@Getter
public class BaseModel {
    private int id;
    private String status;
    private Date createdOn;
    private Date updatedOn;
}
