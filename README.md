# s3ArchiveBuilder
The purpose of this project is to build archive objects suitable for Amazon S3 Glacier. S3 customers who have not designed for data archiving in their cloud storage architectures and who are looking to reduce operational costs associated with non-archival storage classes may find it costly to archive into Glacier via lifecycle policies or S3 Intelligent Tiering. This is particularly the case if the workloads do not align with Cold Storage best practices. Best practices include but are not limited to; reducing the number of objects that will ultimately be archived and organizing objects into archives by an indexing scheme or lookup strategy thereby reducing or eleminating overhead associated with restoring archived objects of interest. 

This project was motivated by a real customer use case consisting of ~ 450 million objects that needed to be archived into Glacier. The objects were relatively small in size and produced by a distibuted application that was commiting objects daily to S3 Standard. When the customer realized the costs associated with archiving this large number of objects they sought out a solution. The AWS Solutions Architecture Team and the customer did extensive reseach to come up with this solution and are sharing it freely with others who are running into a similar problem. 

This source code provides customers interested in this approach with a highly performing and cost effective architecture that can be modified or evolved to meet their business needs. This project should be thought of as a starting point or a framework and should be thoroughly tested in order to ensure that it satisfies the specific use cases or requiments set forth by it's designers.

# Project Overview
s3ArchiveBuilder is a Maven Java Project leveraging the AWS Java SDK (2.0+). It is compatible with Java 8+ JRE, Maven version 3.6+ and developed on the open Eclipse IDE. Upon modifying and building the source code into an executable jar file it can be executed on an EC2 instance satisfying the requirements of the environment or contaniarized to run on AWS EKS (Elastic Kubernetes Service) or AWS ECS (Elastic Container Service). This solution is a distributed producer/consumer architecture leveraging AWS SQS (Simple Queue Service).

# Macro-Architecture Overview
![Optional Text](../master/images/macro-architecture.png)


