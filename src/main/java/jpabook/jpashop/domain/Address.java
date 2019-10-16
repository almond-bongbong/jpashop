package jpabook.jpashop.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter @Builder
public class Address {

	private String city;

	private String street;

	private String zipcode;

	protected Address() {

	}
}
