package com.pryalkin.bs_solutions.service;

import com.pryalkin.bs_solutions.dto.DowntimeDto;
import com.pryalkin.bs_solutions.model.Downtime;
import com.pryalkin.bs_solutions.model.Forklift;
import com.pryalkin.bs_solutions.repository.DowntimeRepository;
import com.pryalkin.bs_solutions.repository.ForkliftRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForkliftService {
    private final ForkliftRepository forkliftRepository;
    private final DowntimeRepository downtimeRepository;

    public List<Forklift> getForklifts(String number) {
        if (number == null || number.trim().isEmpty()) {
            return forkliftRepository.findAll();
        }
        return forkliftRepository.findByNumberContainingIgnoreCase(number);
    }

    @Transactional
    public Forklift saveForklift(Forklift forklift, String username) {
        forklift.setModifiedDate(LocalDateTime.now());
        forklift.setModifiedUser(username); // В реальном приложении брать из SecurityContext
        return forkliftRepository.save(forklift);
    }

    public void deleteForklift(Long id) {
        if (forkliftRepository.hasDowntimes(id)) {
            throw new RuntimeException("Удаление запрещено: у погрузчика есть зарегистрированные простои.");
        }
        forkliftRepository.deleteById(id);
    }

    // Методы для Downtime (маппинг в Dto с расчетом времени)
    public List<DowntimeDto> getDowntimesByForklift(Long forkliftId) {
        return downtimeRepository.findByForkliftIdOrderByStartDateDesc(forkliftId).stream().map(d -> {
            DowntimeDto dto = new DowntimeDto();
            BeanUtils.copyProperties(d, dto);
            dto.setForkliftId(forkliftId);
            dto.calculateDuration();
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public DowntimeDto createDowntime(Long forkliftId, DowntimeDto dto) {
        if (dto.getStartDate() == null) {
            throw new IllegalArgumentException("Дата начала обязательна.");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Описание инцидента обязательно.");
        }

        Forklift forklift = forkliftRepository.findById(forkliftId)
                .orElseThrow(() -> new IllegalArgumentException("Погрузчик не найден: id=" + forkliftId));

        Downtime downtime = new Downtime();
        downtime.setForklift(forklift);
        downtime.setStartDate(dto.getStartDate());
        downtime.setEndDate(dto.getEndDate());
        downtime.setDescription(dto.getDescription().trim());

        Downtime saved = downtimeRepository.save(downtime);

        DowntimeDto out = new DowntimeDto();
        BeanUtils.copyProperties(saved, out);
        out.setForkliftId(forkliftId);
        out.calculateDuration();
        return out;
    }

    @Transactional
    public DowntimeDto updateDowntime(Long downtimeId, DowntimeDto dto) {
        Downtime existing = downtimeRepository.findById(downtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Инцидент не найден: id=" + downtimeId));

        if (dto.getStartDate() == null) {
            throw new IllegalArgumentException("Дата начала обязательна.");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Описание инцидента обязательно.");
        }

        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setDescription(dto.getDescription().trim());

        Downtime saved = downtimeRepository.save(existing);

        DowntimeDto out = new DowntimeDto();
        BeanUtils.copyProperties(saved, out);
        out.setForkliftId(saved.getForklift().getId());
        out.calculateDuration();
        return out;
    }

    @Transactional
    public void deleteDowntime(Long downtimeId) {
        downtimeRepository.deleteById(downtimeId);
    }
}
