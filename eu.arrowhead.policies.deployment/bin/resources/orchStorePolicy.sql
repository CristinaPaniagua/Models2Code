-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: arrowhead
-- ------------------------------------------------------
-- Server version	5.7.21-log
USE `arrowhead`;
SELECT @serviceID :=id FROM service_definition WHERE service_definition='TempRegulationService';
SELECT @providerID :=id FROM system_ WHERE system_name='ControlSystem';
SELECT @consumerID :=id FROM system_ WHERE system_name='ControlSystem';
SELECT @srID :=id FROM service_registry WHERE service_id=@serviceID AND system_id=@providerID;
SELECT @interfaceID :=interface_id FROM service_registry_interface_connection WHERE service_registry_id=@srID;
INSERT INTO `orchestrator_store` (`consumer_system_id`,`provider_system_id`, `service_id`, `service_interface_id`, `priority`) 
VALUES (@consumerID,@providerID,@serviceID,@interfaceID,1);