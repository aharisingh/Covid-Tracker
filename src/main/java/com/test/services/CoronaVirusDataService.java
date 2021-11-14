package com.test.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.test.models.LocationStats;


@Service
public class CoronaVirusDataService {
	private List<LocationStats> allStats = new ArrayList<>();
	
	private long totalCases = 0;
		
	public long getTotalCases() {
		return totalCases;
	}


	public void setTotalCases(long totalCases) {
		this.totalCases = totalCases;
	}


	public List<LocationStats> getAllStats() {
		return allStats;
	}


	public void setAllStats(List<LocationStats> allStats) {
		this.allStats = allStats;
	}
	


	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchCovidData() throws IOException, InterruptedException {
		List<LocationStats> newStats = new ArrayList<>();
		long newCases = 0;
		String COVID_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
		HttpClient client = HttpClient.newHttpClient();
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(COVID_DATA_URL))
				.build();
		
		
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		//System.out.println(httpResponse.body());
		StringReader csvBodyReader = new StringReader(httpResponse.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
				long prevDayCases = 0;
			LocationStats locationStats = new LocationStats();
			locationStats.setState(record.get("Province/State"));
			locationStats.setCountry(record.get("Country/Region"));
			locationStats.setLatestTotalCases(Integer.parseInt(record.get(record.size() - 1)));
			newCases += Integer.parseInt(record.get(record.size() - 1));
			prevDayCases += Integer.parseInt(record.get(record.size() - 2));
			locationStats.setDiffFromPrevDay(newCases - prevDayCases);
			System.out.println(locationStats);
			newStats.add(locationStats);
		}
		
		this.allStats = newStats;
		this.totalCases = newCases;
		
	}
}
