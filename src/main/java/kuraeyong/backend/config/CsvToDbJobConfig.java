package kuraeyong.backend.config;

import kuraeyong.backend.repository.StationTimeTableRepository;
import kuraeyong.backend.domain.StationTimeTableElement;
import kuraeyong.backend.dto.StationTimeTableElementDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CsvToDbJobConfig {
    private final int chunkSize = 1000;

    private final StationTimeTableRepository stationTimeTableRepository;

    private static String csvFilePath;

    @Value("${csv-file-path}")
    public void setCsvFilePath(String path) {
        csvFilePath = path;
    }

    @Bean
    public Job csvToDbJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        log.info("ACCESS csvToDbJob");
        return new JobBuilder("csvToDbJob", jobRepository)
//                .incrementer(new RunIdIncrementer())
                .start(csvToDbStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step csvToDbStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
        log.info("ACCESS csvToDbStep");
        return new StepBuilder("csvToDbStep", jobRepository)
                .<StationTimeTableElementDto, StationTimeTableElement>chunk(chunkSize, platformTransactionManager)
                .reader(csvReader())
                .processor(dtoToEntityProcessor())
                .writer(dbWriter())
                .build();
    }

    @Bean
    public ItemReader<StationTimeTableElementDto> csvReader() {
        log.info("ACCESS csvReader");
        return new FlatFileItemReaderBuilder<StationTimeTableElementDto>()
                .name("csvReader")
                .resource(new FileSystemResource(csvFilePath))
                .delimited().delimiter(",") // 기본 구분자는 ,
                .names(new String[]{"LN_CD", "ORG_STIN_CD", "DAY_CD", "ARV_TM", "DAY_NM", "DPT_TM", "STIN_CD", "TRN_NO", "TMN_STIN_CD", "RAIL_OPR_ISTT_CD"})
//                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
//                    setTargetType(StationTimeTableDto.class);
//                }})
                .fieldSetMapper(fieldSet -> StationTimeTableElementDto.builder()
                        .lnCd(fieldSet.readString("LN_CD"))
                        .orgStinCd(fieldSet.readString("ORG_STIN_CD"))
                        .dayCd(fieldSet.readString("DAY_CD"))
                        .arvTm(fieldSet.readString("ARV_TM"))
                        .dayNm(fieldSet.readString("DAY_NM"))
                        .dptTm(fieldSet.readString("DPT_TM"))
                        .stinCd(fieldSet.readString("STIN_CD"))
                        .trnNo(fieldSet.readString("TRN_NO"))
                        .tmnStinCd(fieldSet.readString("TMN_STIN_CD"))
                        .railOprIsttCd(fieldSet.readString("RAIL_OPR_ISTT_CD"))
                        .build())
                .linesToSkip(1)
                .build();
    }

    @Bean
    public ItemProcessor<StationTimeTableElementDto, StationTimeTableElement> dtoToEntityProcessor() {
        return StationTimeTableElementDto::toEntity;
    }

    @Bean
    public ItemWriter<StationTimeTableElement> dbWriter() {
        log.info("ACCESS dbWriter");
        return items -> stationTimeTableRepository.saveAll(items);
//        return new ItemWriter<StationTimeTable>() {
//            @Override
//            public void write(Chunk<? extends StationTimeTable> items) throws Exception {
//                stationTimeTableRepository.saveAll(items);
//            }
//        };

//        // 테스트
//        return items -> items.forEach(System.out::println);
//        return items -> System.out.println(items.size());
    }
}
