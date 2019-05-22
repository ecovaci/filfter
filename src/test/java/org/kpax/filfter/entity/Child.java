package org.kpax.filfter.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@ToString
@Getter
@Setter
@Entity
public class Child {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long childPk;

	private String strField;

	private int intField;

	private double dblField;

	private BigDecimal bigdField;

	private Date dateField;

	@OneToMany(fetch = FetchType.EAGER,mappedBy = "childParent")
	private List<Subchild> subchildren = new ArrayList<>();

	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
	private Parent parent;

	public Child() {
	}

	public Child(String strField, int intField, double dblField, BigDecimal bigdField, Date dateField) {
		this.strField = strField;
		this.intField = intField;
		this.dblField = dblField;
		this.bigdField = bigdField;
		this.dateField = dateField;
	}
}
