package org.kpax.filfter.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

//ToString
@Getter
@Setter
@Entity
public class Subchild {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long subchildPk;

	private String strField;

	private int intField;

	private double dblField;

	private BigDecimal bigdField;

	private Date dateField;

	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
	private Child childParent;

	public Subchild() {
	}

	public Subchild(String strField, int intField, double dblField, BigDecimal bigdField, Date dateField) {
		this.strField = strField;
		this.intField = intField;
		this.dblField = dblField;
		this.bigdField = bigdField;
		this.dateField = dateField;
	}
}
