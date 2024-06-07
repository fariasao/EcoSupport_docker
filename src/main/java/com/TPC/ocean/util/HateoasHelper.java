package com.TPC.ocean.util;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class HateoasHelper {
    public static <T> EntityModel<T> createModelWithLinks(T entity, Class<?> controllerClass, Long id) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        
        Link selfLink = Link.of(baseUrl + "/" + controllerClass.getSimpleName().toLowerCase() + "/" + id);
        Link deleteLink = Link.of(baseUrl + "/" + controllerClass.getSimpleName().toLowerCase() + "/" + id + "/delete");
        Link contentsLink = Link.of(baseUrl + "/" + controllerClass.getSimpleName().toLowerCase() + "/contents");
        
        return EntityModel.of(entity, selfLink.withSelfRel(), deleteLink.withRel("delete"), contentsLink.withRel("contents"));
    }
}
