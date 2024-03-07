package com.antont.parserui.controller;

import com.antont.parserui.model.News;
import com.antont.parserui.model.NewsPage;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.layout.AnchorPane;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.ws.rs.core.UriBuilder;

public class NewsController {
    @FXML
    private ChoiceBox<String> timeChoiceBox;
    @FXML
    private ListView<News> newsListView;
    @FXML
    private Pagination pagination;

    private final List<String> TIME_STRINGS = List.of("All", "Morning", "Day", "Evening");
    private final String BASE_URL = "http://localhost:8080";
    private final String PAGING_URL = "/news/pageable";

    private final int START_PAGE = 0;
    private final int ITEMS_PER_PAGE = 10;

    // Initializes the UI components and sets up event handlers.
    public void initialize() {
        // Populate the time choice box with predefined time strings and select the first item
        timeChoiceBox.getItems().addAll(TIME_STRINGS);
        timeChoiceBox.setValue(TIME_STRINGS.get(0));
        // Set up an action listener for the time choice box to reload news when a different time is selected
        timeChoiceBox.setOnAction(actionEvent -> loadNews(START_PAGE));
        // Configure the pagination control to load news for the selected page
        // and return a new AnchorPane for each page (though the AnchorPane is not used in this snippet)
        pagination.setPageFactory(pageIndex -> {
            loadNews(pageIndex);
            return new AnchorPane();
        });
        // Initially load the news for the start page
        loadNews(START_PAGE);
    }

    // Loads a page of news into the UI.
    // It fetches the news from the server using the specified page number and the predefined number of items per page.
    private void loadNews(int page) {
        // Fetch the news page from the server
        NewsPage newsList = fetchNewsFromServer(page, ITEMS_PER_PAGE);
        // Update the pagination control with the total number of pages
        pagination.setPageCount(newsList.totalPages());
        // Set the items in the news list view to the content of the fetched news page
        newsListView.getItems().setAll(newsList.content());
        // Configure the list view to use a custom cell factory for displaying news items
        newsListView.setCellFactory(param -> new NewsCell());
    }


    // Fetches a page of news from the server based on the specified page number and size.
    // Handles exceptions by wrapping them in a RuntimeException.
    private NewsPage fetchNewsFromServer(int page, int size) {
        HttpClient client = HttpClient.newHttpClient();

        // Construct the URI for the request, including pagination and time filter parameters
        UriBuilder uriBuilder = UriBuilder
                .fromPath(BASE_URL)
                .path(PAGING_URL)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("time", timeChoiceBox.getSelectionModel().getSelectedItem());

        // Build the GET request with the constructed URI
        HttpRequest request = HttpRequest.newBuilder().uri(uriBuilder.build()).GET().build();
        try {
            // Send the request and receive the response as a string
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            // Configure the ObjectMapper to ignore unknown properties during deserialization
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // Deserialize the response body into a NewsPage object
            return objectMapper.readValue(response.body(), NewsPage.class);
        } catch (Exception e) {
            // Wrap any exceptions in a RuntimeException and rethrow
            throw new RuntimeException("Error during retrieving data from server", e);
        }
    }

    private static class NewsCell extends ListCell<News> {
        @Override
        protected void updateItem(News news, boolean empty) {
            super.updateItem(news, empty);
            if (empty || news == null) {
                setText(null);
            } else {
                // Format and display news details: headline, description, and formatted time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm a");
                String time = news.time().format(formatter);
                setText(news.headline() + "\n" + news.description() + "\n" + time);
            }
        }
    }
}