package com.example.book_club;

import org.junit.Assert;
import org.junit.Test;

import Util.BookApi;

public class userApiTest {
    @Test
    public void user_matches_one_from_api() {
        Assert.assertEquals("test@email.com", BookApi.getInstance().getUsername());
    }
}