package com.tutorial.batchexample.configuration;

import com.tutorial.batchexample.entity.Invoice;
import com.tutorial.batchexample.repository.InvoiceRepository;
import com.tutorial.batchexample.utilities.BlankLineRecordSeparatorPolicy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private InvoiceRepository repository;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public FlatFileItemReader<Invoice> reader() {

        return new FlatFileItemReaderBuilder<Invoice>()
                .name("invoiceItemReader")
                .resource(new ClassPathResource("/invoices.csv"))
                .linesToSkip(1)
                .lineMapper(new DefaultLineMapper<Invoice>(){{
                    setFieldSetMapper(new BeanWrapperFieldSetMapper<Invoice>(){{
                        setTargetType(Invoice.class);
                    }});
                    setLineTokenizer(new DelimitedLineTokenizer(){{
                        setDelimiter(DELIMITER_COMMA);
                        setNames("name", "last_name", "amount", "discount", "location");
                    }});
                }})
                .recordSeparatorPolicy(new BlankLineRecordSeparatorPolicy())
                .build();
    }

    @Bean
    public ItemWriter<Invoice> writer(){
        return items -> {
                    System.out.println("Saving Invoice Records: " + items);
                    repository.saveAll(items);
                };

    }

    @Bean
    public ItemProcessor<Invoice, Invoice> processor() {
       return invoice -> {
               double discount = invoice.getAmount() * (invoice.getDiscount() / 100.0);
               double finalAmount = invoice.getAmount() - discount;
               invoice.setFinalAmount(finalAmount);

               return invoice;
            };
    }

    @Bean
    public JobExecutionListener listener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                System.out.println("Job started at: "+ jobExecution.getStartTime());
                System.out.println("Status of the Job: "+jobExecution.getStatus());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                System.out.println("Job Ended at: "+ jobExecution.getEndTime());
                System.out.println("Status of the Job: "+jobExecution.getStatus());
            }
        };
    }

    @Bean
    public Step stepA(){
        return stepBuilderFactory.get("stepA")
                .<Invoice, Invoice>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job jobA(){
        return jobBuilderFactory.get("jobA")
                .incrementer(new RunIdIncrementer())
                .listener(listener())
                .start(stepA())
                .build();
    }

}
