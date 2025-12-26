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

export type HabitFrequency = 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'CUSTOM';

export interface HabitResponse {
	id: number;
	name: string;
	description: string | null;
	active: boolean;
	frequency: HabitFrequency;
	startDate: string | null;
	endDate: string | null;
	userId: number;
	createdAt: string;
	updatedAt: string;
}

export interface HabitRequest {
	name: string;
	description?: string | null;
	active?: boolean;
	frequency: HabitFrequency;
	startDate?: string | null;
	endDate?: string | null;
}

export interface HabitLogResponse {
	id: number;
	habitId: number;
	logDate: string;
	completed: boolean;
	notes: string | null;
	createdAt: string;
}

export interface HabitLogRequest {
	habitId: number;
	logDate: string;
	completed: boolean;
	notes?: string | null;
}

// ============================================
// Token & User Management
// ============================================

const TOKEN_KEY = 'jwt_token';
const USER_KEY = 'current_user';

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

export function getCurrentUserFromStorage(): UserResponse | null {
	if (!browser) return null;
	const user = localStorage.getItem(USER_KEY);
	return user ? JSON.parse(user) : null;
}

export function setCurrentUser(user: UserResponse): void {
	if (!browser) return;
	localStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function clearCurrentUser(): void {
	if (!browser) return;
	localStorage.removeItem(USER_KEY);
}

export function isAuthenticated(): boolean {
	return getToken() !== null && getCurrentUserFromStorage() !== null;
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

	// Store token and user after successful registration
	setToken(response.token);
	setCurrentUser(response.user);

	return response;
}

export async function login(data: LoginRequest): Promise<AuthResponse> {
	const response = await fetchApi<AuthResponse>('/api/auth/login', {
		method: 'POST',
		body: JSON.stringify(data)
	});

	// Store token and user after successful login
	setToken(response.token);
	setCurrentUser(response.user);

	return response;
}

export function logout(): void {
	clearToken();
	clearCurrentUser();
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
	clearCurrentUser();
}

// ============================================
// Habit API
// ============================================

export async function getHabits(): Promise<HabitResponse[]> {
	const user = getCurrentUserFromStorage();
	if (!user) throw new Error('User not authenticated');
	return fetchApi<HabitResponse[]>(`/api/habits?userId=${user.id}`);
}

export async function getActiveHabits(): Promise<HabitResponse[]> {
	const user = getCurrentUserFromStorage();
	if (!user) throw new Error('User not authenticated');
	return fetchApi<HabitResponse[]>(`/api/habits/active?userId=${user.id}`);
}

export async function getHabit(id: number): Promise<HabitResponse> {
	return fetchApi<HabitResponse>(`/api/habits/${id}`);
}

export async function createHabit(data: HabitRequest): Promise<HabitResponse> {
	const user = getCurrentUserFromStorage();
	if (!user) throw new Error('User not authenticated');
	return fetchApi<HabitResponse>(`/api/habits?userId=${user.id}`, {
		method: 'POST',
		body: JSON.stringify(data)
	});
}

export async function updateHabit(id: number, data: HabitRequest): Promise<HabitResponse> {
	return fetchApi<HabitResponse>(`/api/habits/${id}`, {
		method: 'PUT',
		body: JSON.stringify(data)
	});
}

export async function toggleHabit(id: number): Promise<HabitResponse> {
	return fetchApi<HabitResponse>(`/api/habits/${id}/toggle`, {
		method: 'PATCH'
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
	return fetchApi<HabitLogResponse[]>(`/api/habit-logs?habitId=${habitId}`);
}

export async function getHabitLogsInRange(
	habitId: number,
	startDate: string,
	endDate: string
): Promise<HabitLogResponse[]> {
	return fetchApi<HabitLogResponse[]>(
		`/api/habit-logs/range?habitId=${habitId}&startDate=${startDate}&endDate=${endDate}`
	);
}

export async function getHabitLog(logId: number): Promise<HabitLogResponse> {
	return fetchApi<HabitLogResponse>(`/api/habit-logs/${logId}`);
}

export async function createHabitLog(data: HabitLogRequest): Promise<HabitLogResponse> {
	return fetchApi<HabitLogResponse>('/api/habit-logs', {
		method: 'POST',
		body: JSON.stringify(data)
	});
}

export async function updateHabitLog(
	logId: number,
	data: Partial<HabitLogRequest>
): Promise<HabitLogResponse> {
	return fetchApi<HabitLogResponse>(`/api/habit-logs/${logId}`, {
		method: 'PUT',
		body: JSON.stringify(data)
	});
}

export async function deleteHabitLog(logId: number): Promise<void> {
	return fetchApi<void>(`/api/habit-logs/${logId}`, {
		method: 'DELETE'
	});
}

// Export ApiError for error handling
export { ApiError };
