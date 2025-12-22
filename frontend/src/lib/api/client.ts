/**
 * API Client for Habit Tracker Backend
 *
 * Provides type-safe HTTP client with JWT token management
 */

import { browser } from '$app/environment';
import { env } from '$env/dynamic/public';

const API_BASE_URL = env.PUBLIC_API_URL || 'http://localhost:8080';

// ============================================
// Types matching backend DTOs
// ============================================

export interface UserResponse {
	id: number;
	username: string;
	email: string;
	enabled: boolean;
	createdAt: string;
	updatedAt: string;
}

export interface AuthResponse {
	token: string;
	user: UserResponse;
	message: string;
}

export interface RegisterRequest {
	username: string;
	email: string;
	password: string;
}

export interface LoginRequest {
	usernameOrEmail: string;
	password: string;
}

export interface ErrorResponse {
	timestamp: string;
	status: number;
	error: string;
	message: string;
	path: string;
	validationErrors?: Record<string, string>;
}

export interface HabitResponse {
	id: number;
	name: string;
	description: string | null;
	frequency: string;
	targetCount: number | null;
	userId: number;
	createdAt: string;
	updatedAt: string;
}

export interface HabitLogResponse {
	id: number;
	habitId: number;
	logDate: string;
	count: number;
	note: string | null;
	createdAt: string;
	updatedAt: string;
}

// ============================================
// Token Management
// ============================================

const TOKEN_KEY = 'jwt_token';

export function getToken(): string | null {
	if (!browser) return null;
	return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
	if (!browser) return;
	localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken(): void {
	if (!browser) return;
	localStorage.removeItem(TOKEN_KEY);
}

export function isAuthenticated(): boolean {
	return getToken() !== null;
}

// ============================================
// HTTP Client
// ============================================

class ApiError extends Error {
	constructor(
		public status: number,
		public error: string,
		public details?: ErrorResponse
	) {
		super(error);
		this.name = 'ApiError';
	}
}

async function fetchApi<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
	const url = `${API_BASE_URL}${endpoint}`;

	const headers: HeadersInit = {
		'Content-Type': 'application/json',
		...options.headers
	};

	// Add JWT token if available
	const token = getToken();
	if (token) {
		headers['Authorization'] = `Bearer ${token}`;
	}

	const response = await fetch(url, {
		...options,
		headers
	});

	// Handle error responses
	if (!response.ok) {
		let errorDetails: ErrorResponse | undefined;
		try {
			errorDetails = await response.json();
		} catch {
			// If JSON parsing fails, create basic error
		}

		throw new ApiError(response.status, errorDetails?.message || response.statusText, errorDetails);
	}

	// Handle empty responses (204 No Content)
	if (response.status === 204) {
		return undefined as T;
	}

	return response.json();
}

// ============================================
// Auth API
// ============================================

export async function register(data: RegisterRequest): Promise<AuthResponse> {
	const response = await fetchApi<AuthResponse>('/api/auth/register', {
		method: 'POST',
		body: JSON.stringify(data)
	});

	// Store token after successful registration
	setToken(response.token);

	return response;
}

export async function login(data: LoginRequest): Promise<AuthResponse> {
	const response = await fetchApi<AuthResponse>('/api/auth/login', {
		method: 'POST',
		body: JSON.stringify(data)
	});

	// Store token after successful login
	setToken(response.token);

	return response;
}

export function logout(): void {
	clearToken();
}

// ============================================
// User API
// ============================================

export async function getCurrentUser(): Promise<UserResponse> {
	return fetchApi<UserResponse>('/api/users/me');
}

export async function updateCurrentUser(data: Partial<UserResponse>): Promise<UserResponse> {
	return fetchApi<UserResponse>('/api/users/me', {
		method: 'PUT',
		body: JSON.stringify(data)
	});
}

export async function deleteCurrentUser(): Promise<void> {
	await fetchApi<void>('/api/users/me', {
		method: 'DELETE'
	});
	clearToken();
}

// ============================================
// Habit API
// ============================================

export async function getHabits(): Promise<HabitResponse[]> {
	return fetchApi<HabitResponse[]>('/api/habits');
}

export async function getHabit(id: number): Promise<HabitResponse> {
	return fetchApi<HabitResponse>(`/api/habits/${id}`);
}

export async function createHabit(
	data: Omit<HabitResponse, 'id' | 'userId' | 'createdAt' | 'updatedAt'>
): Promise<HabitResponse> {
	return fetchApi<HabitResponse>('/api/habits', {
		method: 'POST',
		body: JSON.stringify(data)
	});
}

export async function updateHabit(
	id: number,
	data: Partial<HabitResponse>
): Promise<HabitResponse> {
	return fetchApi<HabitResponse>(`/api/habits/${id}`, {
		method: 'PUT',
		body: JSON.stringify(data)
	});
}

export async function deleteHabit(id: number): Promise<void> {
	return fetchApi<void>(`/api/habits/${id}`, {
		method: 'DELETE'
	});
}

// ============================================
// Habit Log API
// ============================================

export async function getHabitLogs(habitId: number): Promise<HabitLogResponse[]> {
	return fetchApi<HabitLogResponse[]>(`/api/habits/${habitId}/logs`);
}

export async function getHabitLog(habitId: number, logId: number): Promise<HabitLogResponse> {
	return fetchApi<HabitLogResponse>(`/api/habits/${habitId}/logs/${logId}`);
}

export async function createHabitLog(
	habitId: number,
	data: Omit<HabitLogResponse, 'id' | 'habitId' | 'createdAt' | 'updatedAt'>
): Promise<HabitLogResponse> {
	return fetchApi<HabitLogResponse>(`/api/habits/${habitId}/logs`, {
		method: 'POST',
		body: JSON.stringify(data)
	});
}

export async function updateHabitLog(
	habitId: number,
	logId: number,
	data: Partial<HabitLogResponse>
): Promise<HabitLogResponse> {
	return fetchApi<HabitLogResponse>(`/api/habits/${habitId}/logs/${logId}`, {
		method: 'PUT',
		body: JSON.stringify(data)
	});
}

export async function deleteHabitLog(habitId: number, logId: number): Promise<void> {
	return fetchApi<void>(`/api/habits/${habitId}/logs/${logId}`, {
		method: 'DELETE'
	});
}

// Export ApiError for error handling
export { ApiError };
