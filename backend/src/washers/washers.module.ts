import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { SupabaseModule } from '../supabase/supabase.module';
import { WashersController } from './washers.controller';
import { WashersService } from './washers.service';

@Module({
  imports: [AuthModule, SupabaseModule],
  controllers: [WashersController],
  providers: [WashersService],
})
export class WashersModule {}
