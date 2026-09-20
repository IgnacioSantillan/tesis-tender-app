import 'reflect-metadata';
import 'dotenv/config';
import { NestFactory } from '@nestjs/core';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { AppModule } from './app.module';
import { ApiExceptionFilter } from './common/api-exception.filter';
import { AppConfig, getAppConfig } from './config/environment';

function setupOpenApi(app: Awaited<ReturnType<typeof NestFactory.create>>, config: AppConfig): void {
  const documentConfig = new DocumentBuilder()
    .setTitle('TenderApp Backend API')
    .setDescription('REST API boundary consumed by the TenderApp Android client.')
    .setVersion(config.apiVersion)
    .addBearerAuth()
    .addApiKey({ type: 'apiKey', name: 'x-qa-key', in: 'header' }, 'qa-key')
    .build();
  const document = SwaggerModule.createDocument(app, documentConfig);

  SwaggerModule.setup(`${config.apiPrefix}/docs`, app, document);
}

async function bootstrap(): Promise<void> {
  const config = getAppConfig();
  const app = await NestFactory.create(AppModule);
  app.setGlobalPrefix(config.apiPrefix);
  app.useGlobalFilters(new ApiExceptionFilter());
  setupOpenApi(app, config);

  await app.listen(config.port, '0.0.0.0');
}

void bootstrap();
