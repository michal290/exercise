package com.hackerrank.sample.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.annotation.JsonValue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.hackerrank.sample.dto.FilteredProducts;
import com.hackerrank.sample.dto.SortedProducts;

@RestController
public class SampleController {


    final String uri = "https://jsonmock.hackerrank.com/api/inventory";
    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(uri, String.class);
    JSONObject root = new JSONObject(result);

    JSONArray data = root.getJSONArray("data");


    @CrossOrigin
    @GetMapping("/filter/price/{initial_price}/{final_price}")
    private ResponseEntity<ArrayList<FilteredProducts>> filtered_books(@PathVariable("initial_price") int init_price, @PathVariable("final_price") int final_price) {

        try {
            ArrayList<FilteredProducts> filteredBooks = new ArrayList<>();
            if(data!=null) {
                filteredBooks = toStream(data)
                                .filter(d -> (Integer) d.get("price") > init_price && (Integer) d.get("price") < final_price)
                                .map(d -> new FilteredProducts(d.getString("barcode")))
                                .collect(Collectors.toCollection(ArrayList::new));
            }
            return new ResponseEntity<ArrayList<FilteredProducts>>(filteredBooks, filteredBooks.isEmpty() ? HttpStatus.BAD_REQUEST : HttpStatus.OK);

        } catch (Exception E) {
            System.out.println("Error encountered : " + E.getMessage());
            return new ResponseEntity<ArrayList<FilteredProducts>>(HttpStatus.NOT_FOUND);
        }

    }


    @CrossOrigin
    @GetMapping("/sort/price")
    private ResponseEntity<SortedProducts[]> sorted_books() {

        try {
            SortedProducts[] SortedProducts = new SortedProducts[]{};
            if(data!=null) {
                SortedProducts = toStream(data)
                        .sorted(Comparator.comparing(d -> (Integer) d.get("price")))
                        .map(d -> new SortedProducts(d.getString("barcode")))
                        .toArray(SortedProducts[]::new);
            }
            return new ResponseEntity<SortedProducts[]>(SortedProducts, HttpStatus.OK);

        } catch (Exception E) {
            System.out.println("Error encountered : " + E.getMessage());
            return new ResponseEntity<SortedProducts[]>(HttpStatus.NOT_FOUND);
        }

    }

    private Stream<JSONObject> toStream(JSONArray data) {
        return StreamSupport.stream(data.spliterator(), false) // false => not parallel stream
                .map(JSONObject.class::cast);
    }


}
