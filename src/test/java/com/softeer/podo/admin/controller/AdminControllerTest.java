package com.softeer.podo.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeer.podo.admin.model.dto.*;
import com.softeer.podo.admin.model.dto.response.EventListResponseDto;
import com.softeer.podo.admin.service.AdminService;
import com.softeer.podo.admin.model.dto.request.ConfigEventRequestDto;
import com.softeer.podo.admin.model.dto.request.ConfigEventRewardRequestDto;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Array;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AdminService adminService;

	@Test
	@Transactional
	@DisplayName("event list 출력 테스트")
	void eventList() throws Exception {
		//given
		when(adminService.getEventList()).thenReturn(new EventListResponseDto(new ArrayList<>()));

		//when
		MvcResult result = mockMvc.perform(get("/admin/eventlist"))
				.andExpect(status().isOk())
				.andReturn();

		//then
		JSONObject response = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(true, response.get("isSuccess"));
		assertEquals(200, response.get("code"));
		assertNotNull(response.getJSONObject("result"));
	}

	@Test
	@Transactional
	@DisplayName("선착순 이벤트 수정 테스트")
	void arrivalEventConfig() throws Exception {
		//given
		String title = "test";
		String description = "testDescription";
		String repeatDay = "1111100";
		LocalTime repeatTime = LocalTime.of(13, 0);
		LocalDateTime startAt = LocalDateTime.of(2024, 9, 6, 13, 0);
		LocalDateTime endAt = LocalDateTime.of(2024, 9, 6, 13, 0);
		String tagImage = "image url";
		ConfigEventRequestDto dto = new ConfigEventRequestDto(title, description, repeatDay, repeatTime, startAt, endAt, tagImage);
		String json = mapper.writeValueAsString(dto);

		//when
		MvcResult result = mockMvc.perform(
				put("/admin/arrival/config")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk())
				.andReturn();

		//then
		JSONObject response = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(true, response.get("isSuccess"));
		assertEquals(200, response.get("code"));
	}

	@Test
	@Transactional
	@DisplayName("랜덤 추첨 이벤트 수정 테스트")
	void lotsEventConfig() throws Exception {
		//given
		String title = "test";
		String description = "testDescription";
		String repeatDay = "1111100";
		LocalTime repeatTime = LocalTime.of(13, 00);
		LocalDateTime startAt = LocalDateTime.of(2024, 9, 6, 13, 00);
		LocalDateTime endAt = LocalDateTime.of(2024, 9, 6, 13, 00);
		String tagImage = "image url";
		ConfigEventRequestDto dto = new ConfigEventRequestDto(title, description, repeatDay, repeatTime, startAt, endAt, tagImage);
		String json = mapper.writeValueAsString(dto);

		//when
		MvcResult result = mockMvc.perform(
						put("/admin/lots/config")
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
				.andExpect(status().isOk())
				.andReturn();

		//then
		JSONObject response = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(true, response.get("isSuccess"));
		assertEquals(200, response.get("code"));
	}

	@Test
	@Transactional
	@DisplayName("선착순 이벤트 상품 수정 테스트")
	void arrivalEventRewardConfig() throws Exception {
		//given
		int rewardNum = 5;

		List<EventRewardDto> eventRewardList = new ArrayList<>();
		for(int i = 1 ; i <= rewardNum ; i++) {
			EventRewardDto rewardDto = new EventRewardDto(i, i, "reward" + i);
			eventRewardList.add(rewardDto);
		}
		ConfigEventRewardRequestDto dto = new ConfigEventRewardRequestDto();
		dto.setEventRewardList(eventRewardList);
		String json = mapper.writeValueAsString(dto);

		//when
		MvcResult result = mockMvc.perform(
						put("/admin/arrival/rewardconfig")
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
				.andExpect(status().isOk())
				.andReturn();

		//then
		JSONObject response = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(true, response.get("isSuccess"));
		assertEquals(200, response.get("code"));
	}

	@Test
	@Transactional
	@DisplayName("랜덤 추첨 이벤트 상품 수정 테스트")
	void lotsEventRewardConfig() throws Exception {
		//given
		int rewardNum = 5;

		List<EventRewardDto> eventRewardList = new ArrayList<>();
		for(int i = 1 ; i <= rewardNum ; i++) {
			EventRewardDto rewardDto = new EventRewardDto(i, i, "reward" + i);
			eventRewardList.add(rewardDto);
		}
		ConfigEventRewardRequestDto dto = new ConfigEventRewardRequestDto();
		dto.setEventRewardList(eventRewardList);
		dto.setEventWeight(new EventWeightDto(3, "comment"));
		String json = mapper.writeValueAsString(dto);

		//when
		MvcResult result = mockMvc.perform(
						put("/admin/lots/rewardconfig")
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
				.andExpect(status().isOk())
				.andReturn();

		//then
		JSONObject response = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(true, response.get("isSuccess"));
		assertEquals(200, response.get("code"));
	}

	@Test
	@Transactional
	@DisplayName("선착순 이벤트 당첨자 목록 출력")
	void arrivalApplicationList() throws Exception {
		//given

		//when
		MvcResult result = mockMvc.perform(get("/admin/arrival/applicationList"))
				.andExpect(status().isOk())
				.andReturn();

		//then
		JSONObject response = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(true, response.get("isSuccess"));
		assertEquals(200, response.get("code"));
	}

	@Test
	@Transactional
	@DisplayName("랜덤 추첨 이벤트 당첨자 목록 출력")
	void lotsApplicationList() throws Exception {
		//given

		//when
		MvcResult result = mockMvc.perform(get("/admin/lots/applicationList"))
				.andExpect(status().isOk())
				.andReturn();

		//then
		JSONObject response = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(true, response.get("isSuccess"));
		assertEquals(200, response.get("code"));
	}

	@Test
	@Transactional
	@DisplayName("랜덤 추첨 이벤트 당첨자 목록 출력")
	void pickRandomLotsUser() throws Exception {
		//given

		//when
		MvcResult result = mockMvc.perform(get("/admin/lots/pickrandom"))
				.andExpect(status().isOk())
				.andReturn();

		//then
		JSONObject response = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(true, response.get("isSuccess"));
		assertEquals(200, response.get("code"));
	}
}