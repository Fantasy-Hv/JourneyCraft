import { request } from './http'
import { mockService } from '@/mock/service'
import { invoke } from './common'
import type { FileAsset } from '@/types/domain'

export function uploadImage(file: File) {
  return invoke(
    async () => (await mockService.file.uploadImage(file)).data as FileAsset,
    () => {
      const formData = new FormData()
      formData.append('file', file)
      return request<FileAsset>({ url: '/file/upload/image', method: 'POST', data: formData })
    },
  )
}

export function uploadVideo(file: File) {
  return invoke(
    async () => (await mockService.file.uploadVideo(file)).data as FileAsset,
    () => {
      const formData = new FormData()
      formData.append('file', file)
      return request<FileAsset>({ url: '/file/upload/video', method: 'POST', data: formData })
    },
  )
}

export function getFile(key: string) {
  return invoke(
    async () => (await mockService.file.detail(key)).data as FileAsset,
    () => request<FileAsset>({ url: `/file/${key}`, method: 'GET' }),
  )
}

export function deleteFile(key: string) {
  return invoke(
    async () => (await mockService.file.remove(key)).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: `/file/${key}`, method: 'DELETE' }),
  )
}

