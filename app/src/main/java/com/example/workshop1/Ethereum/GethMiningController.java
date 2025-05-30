package com.example.workshop1.Ethereum;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class GethMiningController {
    private static final String RPC_URL = BlockchainConfig.BLOCKCHAIN_URL;

    public static CompletableFuture<Boolean> startMining(int threads) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JSONObject request = new JSONObject();
                request.put("jsonrpc", "2.0");
                request.put("method", "miner_start");

                JSONArray params = new JSONArray();
                params.put(threads); 
                request.put("params", params);
                request.put("id", 1);

                String response = sendRPCRequest(request.toString());
                Log.d("Mining", "Start mining response: " + response);

                JSONObject jsonResponse = new JSONObject(response);
                return !jsonResponse.has("error");

            } catch (Exception e) {
                Log.e("Mining", "Error starting mining: " + e.getMessage());
                return false;
            }
        });
    }

    public static CompletableFuture<Boolean> stopMining() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JSONObject request = new JSONObject();
                request.put("jsonrpc", "2.0");
                request.put("method", "miner_stop");

                JSONArray params = new JSONArray();
                request.put("params", params);
                request.put("id", 2);

                String response = sendRPCRequest(request.toString());
                Log.d("Mining", "Stop mining response: " + response);

                JSONObject jsonResponse = new JSONObject(response);
                return !jsonResponse.has("error");

            } catch (Exception e) {
                Log.e("Mining", "Error stopping mining: " + e.getMessage());
                return false;
            }
        });
    }

    public static CompletableFuture<Boolean> isMining() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JSONObject request = new JSONObject();
                request.put("jsonrpc", "2.0");
                request.put("method", "eth_mining");

                JSONArray params = new JSONArray();
                request.put("params", params);
                request.put("id", 3);

                String response = sendRPCRequest(request.toString());
                Log.d("Mining", "Mining status response: " + response);

                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.has("result")) {
                    return jsonResponse.getBoolean("result");
                }
                return false;

            } catch (Exception e) {
                Log.e("Mining", "Error checking mining status: " + e.getMessage());
                return false;
            }
        });
    }

    private static String sendRPCRequest(String jsonRequest) throws Exception {
        URL url = new URL(RPC_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonRequest.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Check response code
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("HTTP error code: " + responseCode);
        }

        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return response.toString();
    }
}