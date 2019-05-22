package org.kpax.filfter.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//@ToString
@Getter
@Setter
@Entity
public class Parent {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long parentPk;

	private String strField;
	private int intField;
	private double dblField;
	private BigDecimal bigdField;

	@OneToMany(fetch = FetchType.EAGER,mappedBy = "parent")
	private List<Child> children = new ArrayList<>();

	public Parent() {
	}

	public Parent(String strField, int intField, double dblField, BigDecimal bigdField) {
		this.strField = strField;
		this.intField = intField;
		this.dblField = dblField;
		this.bigdField = bigdField;
	}
}
