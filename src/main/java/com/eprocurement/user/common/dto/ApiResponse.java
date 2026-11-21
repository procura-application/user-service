package com.eprocurement.user.common.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiResponse<T> {

	@Enumerated(EnumType.ORDINAL)
	private int status;
	private String message;
	private T data;
	private List<String> errorList;

}
