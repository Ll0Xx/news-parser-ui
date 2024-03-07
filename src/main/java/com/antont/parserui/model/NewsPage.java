package com.antont.parserui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record NewsPage(List<News> content, Integer totalPages, Integer size) { }
