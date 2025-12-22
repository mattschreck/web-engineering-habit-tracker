package com.example.habittracker.exception

class ResourceNotFoundException(message: String) : RuntimeException(message)

class ResourceAlreadyExistsException(message: String) : RuntimeException(message)

class BadRequestException(message: String) : RuntimeException(message)

class UnauthorizedException(message: String) : RuntimeException(message)

class ForbiddenException(message: String) : RuntimeException(message)
