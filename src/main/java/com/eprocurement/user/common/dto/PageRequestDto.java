package com.eprocurement.user.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDto {

    private int pageNumber=0;
    private int pageSize=10;
    private String sort="createdDate";
    private String search;
}
