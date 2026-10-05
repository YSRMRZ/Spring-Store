package com.codewithmosh.store.mappers;

import com.codewithmosh.store.DTO.ProductDto;
import com.codewithmosh.store.DTO.UpdateUserRequest;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source ="category.id" , target = "categoryId")
    ProductDto toDto(Product product);

    Product toEntity(ProductDto request);

    @Mapping(target = "id" , ignore = true)
    void update(ProductDto request, @MappingTarget Product product);

}
