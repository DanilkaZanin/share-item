package com.example.demo.service;

public interface EntityService<T> {
    T getById(Long id);
}
