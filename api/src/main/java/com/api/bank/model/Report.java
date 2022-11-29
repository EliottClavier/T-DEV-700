package com.api.bank.model;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Report extends Base{
    private String label;
}
