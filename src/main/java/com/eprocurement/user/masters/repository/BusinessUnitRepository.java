package com.eprocurement.user.masters.repository;

import com.eprocurement.user.masters.entity.BusinessUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessUnitRepository extends JpaRepository<BusinessUnit,String> {

}
