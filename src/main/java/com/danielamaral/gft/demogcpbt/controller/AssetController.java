package com.danielamaral.gft.demogcpbt.controller;

import com.danielamaral.gft.demogcpbt.model.Asset;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/api")
public class AssetController {


    @GetMapping("/asset/{assetKey}")
    public Asset getAssetsByKey(@PathVariable String assetKey) {
        return new Asset();
    }

    @GetMapping("/asset/{assetKey}/start/{start}/end/{end}")
    public List<Asset> getAssetByRange(@PathVariable String assetKey, @PathVariable String start, @PathVariable String end) {

        List<Asset> assetList = new ArrayList<>();
        return assetList;
    }

    @PostMapping("/asset/load")
    public void loadAsset() {
    }

    @GetMapping("/admin/config")
    public String getConfiguration() {
        return "config";
    }

    @PostMapping("/admin/table")
    public void createTable() {

    }
}