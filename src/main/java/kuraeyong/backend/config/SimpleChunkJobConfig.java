package kuraeyong.backend.config;

import kuraeyong.backend.dto.StationTimeTableDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class SimpleChunkJobConfig {
    private final int chunkSize = 1000;

    @Bean
    public Job simpleChunkJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        log.info("ACCESS simpleChunkJob");
        return new JobBuilder("simpleChunkJob", jobRepository)
//                .incrementer(new RunIdIncrementer())
                .start(simpleChunkStep1(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step simpleChunkStep1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
        log.info("ACCESS simpleChunkStep1");
        return new StepBuilder("simpleChunkStep1", jobRepository)
                .<StationTimeTableDto, StationTimeTableDto>chunk(chunkSize, platformTransactionManager)
                .reader(flatFileReader())
                .writer(simpleChunkWriter())
                .build();
    }

    @Bean
    public ItemReader<StationTimeTableDto> flatFileReader() {
        log.info("ACCESS itemReader");
        return new FlatFileItemReaderBuilder<StationTimeTableDto>()
                .name("itemReader")
                .resource(new FileSystemResource("src/main/resources/station_time_table_saturday.csv"))
                .delimited()
                .names(new String[]{"LN_CD", "ORG_STIN_CD", "DAY_CD", "ARV_TM", "DAY_NM", "DPT_TM", "STIN_CD", "TRN_NO", "TMN_STIN_CD", "RAIL_OPR_ISTT_CD"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(StationTimeTableDto.class);
                }})
                .build();
    }

    @Bean
    public ItemWriter<StationTimeTableDto> simpleChunkWriter() {
        log.info("ACCESS itemWriter");
        return items -> items.forEach(System.out::println);
//        return items -> System.out.println(items.size());
    }
}
